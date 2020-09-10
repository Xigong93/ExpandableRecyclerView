package pokercc.android.expandablerecyclerview.sample

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.View.OnSystemUiVisibilityChangeListener
import android.view.WindowManager


/**
 * 状态栏
 * 功能:
 * - 用来占位:高度为系统状态栏高度
 * - 设置状态栏颜色:setBackgroundColor 就可以了
 * - 设置系统状态显示的颜色:是否是黑色
 * - 设置系统状态栏是否显示
 * Created by pokercc on 2020-4-13 17:31:07
 */
class StateBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    /** 亮的状态栏 */
    var lightStateBar: Boolean = true
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val vis = systemUiVisibility
                systemUiVisibility = if (value) {
                    vis or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }
    /** 状态栏高度 */
    private var statusBarHeight = 0

    /** 系统状态栏是否显示 */
    private var stateBarVisible: Boolean = true
        set(value) {
            field = value
            context.asActivity()?.apply {
                val params: WindowManager.LayoutParams = window.attributes
                if (!value) {
                    params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                } else {
                    params.flags = params.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                }
                window.attributes = params
            }

        }

    init {
        setOnApplyWindowInsetsListener { v, insets ->
            statusBarHeight = insets.systemWindowInsetTop // status bar height
            insets
        }

        context.obtainStyledAttributes(attrs, R.styleable.StateBar, defStyleAttr, 0).apply {
            lightStateBar = getBoolean(R.styleable.StateBar_lightStateBar, true)
            stateBarVisible = getBoolean(R.styleable.StateBar_stateBarVisible, true)
        }.recycle()
        // 设置沉浸式
        context.asActivity()?.fullScreen()
        //隐藏系统UI
        setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener { visibility ->
            //隐藏虚拟按键
            if (stateBarVisible.not() && visibility and SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                postDelayed({
                    stateBarVisible = false
                }, 2000)
            }
        })
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (statusBarHeight == 0) {
            statusBarHeight = getStatusBarHeight()
        }
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(statusBarHeight, MeasureSpec.EXACTLY)
        )
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}