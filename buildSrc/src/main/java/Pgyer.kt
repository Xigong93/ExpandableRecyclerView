import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.logging.progress.ProgressLoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

open class UploadConfig(
    /** app名称 */
    var appName: String? = null,
    /** app下载的主页 */
    var appDownloadUrl: String? = null,
    /** apk的相对路径 */
    var apkPath: String = "outputs/apk/debug/app-debug.apk",
    /** 蒲公英上传的token */
    var pgyerApiKey: String = "",
    /** 企业微信的机器人链接 */
    var wechatWorkRobotUrl: String = "",
    /** 飞书的机器人链接 */
    var feishuRobotUrl: String = "",
    /** 测试的手机号,用来发送通知自动@ */
    var testerPhones: List<String> = listOf()
)

class UploadPgyer : Plugin<ProjectInternal> {

    override fun apply(target: ProjectInternal) {
        target.extensions.create("UploadConfig", UploadConfig::class.java)
        target.task("uploadPgyer") {
            description = "上传apk文件到pgyer平台"
            group = "upload"
            dependsOn("assembleDebug")
            mustRunAfter("clean")

            val descFile = project.file("upload-pgyer-desc.txt")
            if (!descFile.exists()) {
                descFile.parentFile?.mkdirs()
                descFile.createNewFile()
            }

            doLast {
                println("上传apk到pgyer")
                val uploadConfig = project.extensions.getByType(UploadConfig::class.java)
                // 检查更新描述文件
                val updateDesc = descFile.readText(Charset.forName("utf-8"))
                println(updateDesc)
                if (updateDesc.isEmpty()) {
                    throw RuntimeException("请填写更新描述到[$descFile]中")
                }
                // 开始上传
                uploadApk(
                    File(project.buildDir, uploadConfig.apkPath),
                    updateDesc,
                    uploadConfig.pgyerApiKey,
                    target.services[ProgressLoggerFactory::class.java]
                )
                // 发送通知
                val message =
                    "Dear tester:\n《${uploadConfig.appName}》 已经更新\n$updateDesc\n${uploadConfig.appDownloadUrl}\n安装密码:123456"
                if (uploadConfig.wechatWorkRobotUrl.isNullOrEmpty()) {
                    println("未配置企业微信机器人")
                } else {
                    sendWeworkMessage(
                        message,
                        uploadConfig.wechatWorkRobotUrl,
                        uploadConfig.testerPhones
                    )
                }
                if (uploadConfig.feishuRobotUrl.isNullOrEmpty()) {
                    println("未配置飞书机器人")
                } else {
                    val title = "《${uploadConfig.appName}》 已经更新"
                    val feishuMessage = "$updateDesc\n${uploadConfig.appDownloadUrl}\n安装密码:123456"
                    sendFeiShuMessage(
                        title,
                        feishuMessage,
                        uploadConfig.feishuRobotUrl,
                        uploadConfig.testerPhones
                    )
                }

                // 删除重置描述文件
                descFile.delete()
                descFile.createNewFile()
                println("上传完成!")
            }
        }
    }

}


internal class ProgressRequestBody(
    private val originRequestBody: RequestBody,
    private val progressListener: ProgressListener? = null
) : RequestBody() {
    override fun contentType(): MediaType? = originRequestBody.contentType()
    override fun contentLength(): Long {
        return originRequestBody.contentLength()
    }

    override fun isDuplex(): Boolean {
        return originRequestBody.isDuplex()
    }

    override fun isOneShot(): Boolean {
        return originRequestBody.isOneShot()
    }

    override fun writeTo(sink: BufferedSink) {
        val bufferedSink = ProgressSink(sink).buffer()
        originRequestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    inner class ProgressSink(delegate: Sink) : ForwardingSink(delegate) {
        private var byteLength = 0L
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            this.byteLength += byteCount
            progressListener?.update(byteLength, contentLength(), byteLength == contentLength())
        }
    }

}

internal interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}


internal class ConsoleProgressListener(
    private val fileName: String,
    progressLoggerFactory: ProgressLoggerFactory
) : ProgressListener {


    private var percent = 0L
    private val progressLogger = progressLoggerFactory.newOperation("上传apk文件")

    private var started = false
    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        val percent = bytesRead * 100 / contentLength
        if (this.percent == percent /*|| percent - this.precent < 20*/) {
            return
        }
        this.percent = percent

        if (!started) {
            started = true
            progressLogger.start("上传apk文件", "上传中")
        }
        progressLogger.progress("\r正在上传${fileName},已完成${this.percent}%")
        if (done) {
            progressLogger.completed("上传成功", false)
        }

    }

}

/**
 * 使用okhttp
 */
private fun uploadApk(
    apkFile: File,
    updateDesc: String,
    pgyerApiKey: String,
    progressLoggerFactory: ProgressLoggerFactory
) {

    println("开始上传 ${apkFile.name}")
    //https://www.pgyer.com/doc/view/api?xcx=1
    val okHttpClient = OkHttpClient()
    val multipartBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("_api_key", pgyerApiKey)
        .addFormDataPart("buildUpdateDescription", updateDesc)
        .addFormDataPart(
            "buildInstallType",
            1.toString()
        )//(必填)应用安装方式，值为(2,3，4)。2：密码安装，3：邀请安装，4：回答问题安装。
        .addFormDataPart("buildPassword", "123456")
        .addFormDataPart(
            "file",
            apkFile.name,
            ProgressRequestBody(
                apkFile.asRequestBody(),
                ConsoleProgressListener(apkFile.name, progressLoggerFactory)
            )
        )
        .build()
    val request = Request.Builder()
        .url("https://www.pgyer.com/apiv2/app/upload")
        .post(multipartBody)
        .build()
    val response = okHttpClient.newCall(request).execute()
    if (!response.isSuccessful) {
        throw IOException("上传失败，http code is ${response.code}")
    }
    val responseBody = response.body?.string()
    val result = Gson().fromJson(responseBody, JsonObject::class.java)
    if (result.get("code").asInt != 0) {
        println(responseBody)
        throw IOException("上传失败,${result.get("message").asString}")
    }
    println("上传成功 ${apkFile.name}")


}

/**
 * 发送企业微信消息
 */
private fun sendWeworkMessage(
    message: String,
    wechatWorkRobotUrl: String,
    testerPhones: List<String>
) {

    val body = JsonObject()
        .also {
            it.addProperty("msgtype", "text")

            it.add("text", JsonObject().also { markdown ->
                markdown.addProperty("content", message)
                markdown.add("mentioned_mobile_list", JsonArray().also { mobileList ->
                    testerPhones.forEach(mobileList::add)
                })
            })
        }.toString()
    val request = Request
        .Builder()
        .url(wechatWorkRobotUrl)// 机器人地址
        .post(body.toRequestBody("application/json".toMediaType()))
        .build()
    val response = OkHttpClient().newCall(request).execute()
    if (!response.isSuccessful) {
        throw IOException("发送消息失败,http code:${response.code}")
    }
    val resultText = response.body!!.string()
    val result = Gson().fromJson(resultText, JsonObject::class.java)
    if (result.get("errcode").asInt != 0) {
        throw IOException("发送消息失败,${resultText}")
    }
    println("发送企业微信通知成功")
}

/**
 * 发送企业微信消息
 */
private fun sendFeiShuMessage(
    title: String,
    message: String,
    feishuRobotUrl: String,
    testerPhones: List<String>
) {

    val body = JsonObject()
        .apply {
            addProperty("title", title)
            addProperty("text", message)
        }.toString()
    val request = Request
        .Builder()
        .url(feishuRobotUrl)// 机器人地址
        .post(body.toRequestBody("application/json".toMediaType()))
        .build()
    val response = OkHttpClient().newCall(request).execute()
    if (!response.isSuccessful) {
        throw IOException("发送消息失败,http code:${response.code}")
    }
    val resultText = response.body!!.string()
    val result = Gson().fromJson(resultText, JsonObject::class.java)
    require(result.get("ok").asBoolean) { "发送消息失败,${resultText}" }
    println("发送飞书通知成功")

}
