package pokercc.android.expandablerecyclerview

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoExpandableAdapterTest {

    private lateinit var adapter: ExpandableAdapter<*>

    @Before
    fun setUp() {
        adapter = DemoExpandableAdapter()
    }

    @Test
    fun testGetCount() {
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun testExpand() {
        adapter.expandGroup(0, false)
        assertThat(adapter.itemCount).isEqualTo(5)
    }

    @Test
    fun testIsExpand() {
        adapter.expandGroup(0, false)
        assertThat(adapter.isExpand(0)).isTrue()
        assertThat(adapter.isExpand(1)).isFalse()
    }

    @Test
    fun testCollapse() {
        adapter.expandGroup(0, false)
        adapter.collapseGroup(0, false)
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun testExpandAll() {
        adapter.expandAllGroup()
        assertThat(adapter.itemCount).isEqualTo(9)
    }

    @Test
    fun testCollapseAll() {
        adapter.expandAllGroup()
        adapter.collapseAllGroup()
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun testOnlyExpandOneGroup() {
        // Only one group expand  is false
        adapter.onlyOneGroupExpand = false
        adapter.expandGroup(0, false)
        adapter.expandGroup(1, false)
        assertThat(adapter.itemCount).isEqualTo(7)
        // Only one group expand  is true
        adapter.onlyOneGroupExpand = true
        adapter.collapseAllGroup()
        adapter.expandGroup(0, false)
        adapter.expandGroup(1, false)
        assertThat(adapter.itemCount).isEqualTo(5)
    }

    @Test
    fun testGetGroupAdapterPosition() {
        assertThat(adapter.getGroupAdapterPosition(1)).isEqualTo(1)
        assertThat(adapter.getGroupAdapterPosition(2)).isEqualTo(2)
    }

    @Test
    fun testGetChildAdapterPosition() {
        assertThat(adapter.getChildAdapterPosition(0, 0)).isEqualTo(-1)
        assertThat(adapter.getChildAdapterPosition2(0, 0)).isNull()
        adapter.expandGroup(0, false)
        assertThat(adapter.getChildAdapterPosition(0, 0)).isEqualTo(1)
        assertThat(adapter.getChildAdapterPosition2(0, 0)).isEqualTo(1)
    }

    @Test
    @UiThreadTest
    fun testGetItemAdapterPosition() {
        // Collapse
        for (i in 0 until 3) {
            adapter.getItemAdapterPosition(i).apply {
                assertThat(groupPosition).isEqualTo(i)
                assertThat(childPosition).isNull()
            }
        }
        // Expand all
        adapter.expandAllGroup()
        for (i in 0 until 3) {
            adapter.getItemAdapterPosition(adapter.getGroupAdapterPosition(i)).apply {
                assertThat(groupPosition).isEqualTo(i)
                assertThat(childPosition).isNull()
            }
        }
        for (i in 0 until 3) {
            for (j in 0 until 2) {
                adapter.getItemAdapterPosition(adapter.getChildAdapterPosition2(i, j)!!).apply {
                    assertThat(groupPosition).isEqualTo(i)
                    assertThat(childPosition).isEqualTo(j)
                }
            }
        }
    }


}