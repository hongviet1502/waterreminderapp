package vn.com.rd.waterreminder.data.repository

import vn.com.rd.waterreminder.data.dao.UserDao
import vn.com.rd.waterreminder.data.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun getActiveUser(): User? = userDao.getActiveUser()

    suspend fun createUser(user: User): Long = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
}