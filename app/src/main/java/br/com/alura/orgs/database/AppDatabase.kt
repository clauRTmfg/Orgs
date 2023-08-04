package br.com.alura.orgs.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.alura.orgs.database.converter.Converters
import br.com.alura.orgs.database.dao.ProdutoDAO
import br.com.alura.orgs.model.Produto

@Database(entities = [Produto::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun produtoDAO(): ProdutoDAO

    companion object {

        @Volatile
        private lateinit var db: AppDatabase

        fun instancia(context: Context): AppDatabase {
            if (::db.isInitialized) return db
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "orgs.db"
            )
                // allowMainThreadQueries permite que os acessos ao database
                    // sejam feitos na thread principal, e não deve ser utilizado
                    // a não ser em testes
                //.allowMainThreadQueries()
                .build()
                .also {
                    db = it
                    Log.i("AppDatabase", ": instancia criada")
                }
        }
    }
}