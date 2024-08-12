package digital.fischers.coinsaw.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        User::class,
        Group::class,
        Bill::class,
        CalculatedTransaction::class,
        Changelog::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CoinsawDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun billDao(): BillDao
    abstract fun calculatedTransactionDao(): CalculatedTransactionDao
    abstract fun changelogDao(): ChangelogDao

    companion object {
        @Volatile
        private var INSTANCE: CoinsawDatabase? = null

        fun getDatabase(context: Context): CoinsawDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, CoinsawDatabase::class.java, "coinsaw_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
