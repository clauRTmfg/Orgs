package br.com.alura.orgs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.alura.orgs.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDAO {

    @Query("SELECT * FROM Produto")
    //suspend fun buscaTodos() : List<Produto>
    // utilizando o Flow, removemos a clausula suspend
    fun buscaTodos() : Flow<List<Produto>>

    @Query("SELECT * FROM Produto ORDER BY nome")
     fun buscaPorNome() : Flow<List<Produto>>

    @Query("SELECT * FROM Produto ORDER BY nome DESC")
     fun buscaPorNomeDesc() : Flow<List<Produto>>

    @Query("SELECT * FROM Produto ORDER BY descricao")
     fun buscaPorDescricao() : Flow<List<Produto>>

    @Query("SELECT * FROM Produto ORDER BY descricao DESC")
     fun buscaPorDescricaoDesc() : Flow<List<Produto>>

    @Query("SELECT * FROM Produto ORDER BY valor")
     fun buscaPorValor() : Flow<List<Produto>>

    @Query("SELECT * FROM Produto ORDER BY valor DESC")
     fun buscaPorValorDesc() : Flow<List<Produto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salva(produto: Produto)

    @Delete
    suspend fun remove(produto: Produto)

    @Update
    suspend fun atualiza(produto: Produto)

    @Query("SELECT * FROM Produto WHERE id = :id")
    suspend fun buscaPorId(id: Long) : Produto?

}