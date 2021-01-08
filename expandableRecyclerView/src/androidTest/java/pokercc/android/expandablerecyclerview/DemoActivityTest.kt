package pokercc.android.expandablerecyclerview

import androidx.core.view.children
import androidx.core.view.iterator
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class DemoActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(DemoActivity::class.java)

    @Test
    fun testGetItemLayoutPosition() {
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityRule.scenario.onActivity { activity ->
            val adapter = activity.expandableAdapter
            val recyclerView = activity.recyclerView
            for (view in recyclerView) {
                val holder = recyclerView.getChildViewHolder(view)
                val itemPosition = adapter.getItemLayoutPosition(holder)
                assertThat(itemPosition.groupPosition).isAtLeast(0)
                assertThat(itemPosition.childPosition).isNull()
            }
        }
    }

    @Test
    fun testGetItemAdapterPosition() {
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityRule.scenario.onActivity { activity ->
            val adapter = activity.expandableAdapter
            val recyclerView = activity.recyclerView
            for (view in recyclerView) {
                val holder = recyclerView.getChildViewHolder(view)
                val itemPosition = adapter.getItemAdapterPosition(holder)
                assertThat(itemPosition?.groupPosition).isAtLeast(0)
                assertThat(itemPosition?.childPosition).isNull()
            }
        }
    }

    @Test
    fun testAdapterPositionEqualsLayoutPosition() {
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityRule.scenario.onActivity { activity ->
            val adapter = activity.expandableAdapter
            val recyclerView = activity.recyclerView
            for (view in recyclerView) {
                val holder = recyclerView.getChildViewHolder(view)
                val adapterPosition = adapter.getItemAdapterPosition(holder)
                val layoutPosition = adapter.getItemLayoutPosition(holder)
                assertThat(adapterPosition).isEqualTo(layoutPosition)
            }
        }
    }

    @Test
    fun testFastCreateViewHolder() {
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityRule.scenario.onActivity { activity ->
            val adapter = activity.expandableAdapter
            val recyclerView = activity.recyclerView
            assertThat(recyclerView.children
                .map { recyclerView.getChildViewHolder(it) }
                .any { adapter.getItemAdapterPosition(it) == null }).isFalse()
            adapter.expandGroup(0, false)
            assertThat(recyclerView.children
                .map { recyclerView.getChildViewHolder(it) }
                .any { adapter.getItemAdapterPosition(it) == null }).isTrue()
        }
    }


}