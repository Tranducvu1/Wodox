import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.starnest.data.studio.datasource.dao.DrawingDao
import com.starnest.data.studio.datasource.entity.DrawEntity
import com.starnest.data.studio.datasource.mapper.DrawingItemMapper
import com.starnest.domain.studio.model.DrawItem
import com.starnest.domain.studio.repository.DrawingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class DrawItemRepositoryImpl(
    private val dao: DrawingDao,
    private val mapper: DrawingItemMapper
) : DrawingItemRepository {
    override fun getAllDrawItemPaging(): Flow<PagingData<DrawItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getAllDrawItem() }
        ).flow.map { pagingData->
            pagingData.map { entity ->
                mapper.mapToDomain(entity.mapToDrawAndLayer())
            }
        }
    }

    override suspend fun save(drawItem: DrawItem): DrawItem? {
        val entity = mapper.mapToEntity(drawItem).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

    override fun getAllDrawItem(): List<DrawItem> {
        return mapper.mapToDomainList(dao.getDrawItems())
    }
}
