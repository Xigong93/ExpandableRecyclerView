package pokercc.android.expandablerecyclerview

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExpandableAdapterTest {
    private lateinit var adapter: ExpandableAdapter<*>

    @Before
    fun setUp() {
        adapter = spy(TestExpandableAdapter())
    }

    @Test
    fun testGetCount() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun testExpand() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        adapter.expandGroup(0, false)
        assertThat(adapter.itemCount).isEqualTo(5)
    }

    @Test
    fun testIsExpand() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        adapter.expandGroup(0, false)
        assertThat(adapter.isExpand(0)).isTrue()
        assertThat(adapter.isExpand(1)).isFalse()
    }

    @Test
    fun testCollapse() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        adapter.expandGroup(0, false)
        adapter.collapseGroup(0, false)
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun testExpandAll() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        adapter.expandAllGroup()
        assertThat(adapter.itemCount).isEqualTo(9)
    }

    @Test
    fun testCollapseAll() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        adapter.expandAllGroup()
        adapter.collapseAllGroup()
        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun testOnlyExpandOneGroup() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
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
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        assertThat(adapter.getGroupAdapterPosition(1)).isEqualTo(1)
        assertThat(adapter.getGroupAdapterPosition(2)).isEqualTo(2)
    }

    @Test
    fun testGetChildAdapterPosition() {
        doReturn(3).`when`(adapter).getGroupCount()
        doReturn(2).`when`(adapter).getChildCount(anyInt())
        assertThat(adapter.getChildAdapterPosition(0,0)).isEqualTo(1)
        assertThat(adapter.getChildAdapterPosition(1,0)).isEqualTo(2)
    }
}