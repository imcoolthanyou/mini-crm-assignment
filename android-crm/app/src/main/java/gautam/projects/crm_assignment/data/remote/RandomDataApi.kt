package gautam.projects.crm_assignment.data.remote
import gautam.projects.crm_assignment.data.remote.dto.User
import retrofit2.Response
import retrofit2.http.GET

interface RandomDataApi {
    @GET("users")
    suspend fun getRandomUsers(): Response<List<User>>
}