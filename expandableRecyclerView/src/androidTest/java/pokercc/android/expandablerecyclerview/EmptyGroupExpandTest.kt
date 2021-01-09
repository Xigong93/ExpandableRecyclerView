package pokercc.android.expandablerecyclerview

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test enable empty group expand and collapse.
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class EmptyGroupExpandTest {
    private lateinit var adapter: ExpandableAdapter<*>

    @Before
    fun setUp() {
        adapter = DemoExpandableAdapter(1, 0)
    }

    @Test
    fun testExpand() {
        adapter.expandGroup(0, false)
    }

    @Test
    fun testCollapse() {
        adapter.collapseGroup(0, false)
    }

    @Test
    fun testGetChildAdapterPosition() {
        adapter.expandGroup(0, false)
        val position = adapter.getChildAdapterPosition2(0, 0)
        assertThat(position).isNull()
    }
}