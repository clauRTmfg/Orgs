package br.com.alura.orgs.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosBinding
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ListaProdutosActivity : AppCompatActivity() {

    private val adapter by lazy {
        ListaProdutosAdapter(context = this)
    }
    private val binding by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }

    private var tipoDeOrdenacao = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //funcaoTesteRunBlocking()
        //funcaoTesteJob()

        setContentView(binding.root)
        configRecyclerView()
        configFAB()
        configAtualizacaoProdutos(tipoDeOrdenacao)
    }


    override fun onResume() {
        super.onResume()
        // Isto poderia ser feito aqui (antes de usar o Binding),
        // mas foi feito no arquivo de layout
        //recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // utilização do cancel no fim de uma activity
    // isto não é necessário com o lifecycleScope
//    override fun onDestroy() {
//        super.onDestroy()
//        job.cancel()
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.produtos_ordenacao_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        tipoDeOrdenacao = item.itemId
        configAtualizacaoProdutos(tipoDeOrdenacao)
        return super.onOptionsItemSelected(item)
    }

    private fun configAtualizacaoProdutos(tipoDeOrdenacao: Int) {

        val db = AppDatabase.instancia(this)
        val produtoDAO = db.produtoDAO()
        var fluxoProdutos: Flow<List<Produto>> = emptyFlow()

        // ao invés de usar try/catch nas coroutines
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            // aqui vai o código equivalente ao catch
        }

        when (tipoDeOrdenacao) {
            R.id.menu_item_ordena_nome -> fluxoProdutos = produtoDAO.buscaPorNome()
            R.id.menu_item_ordena_nome_desc -> fluxoProdutos =
                produtoDAO.buscaPorNomeDesc()
            R.id.menu_item_ordena_descricao -> fluxoProdutos =
                produtoDAO.buscaPorDescricao()
            R.id.menu_item_ordena_descricao_desc -> fluxoProdutos =
                produtoDAO.buscaPorDescricaoDesc()
            R.id.menu_item_ordena_valor -> fluxoProdutos = produtoDAO.buscaPorValor()
            R.id.menu_item_ordena_valor_desc -> fluxoProdutos =
                produtoDAO.buscaPorValorDesc()
            R.id.menu_item_sem_ordenacao -> fluxoProdutos = produtoDAO.buscaTodos()
            else -> fluxoProdutos = produtoDAO.buscaTodos()
        }


        // neste escopo , as coroutines são executadas na Main thread
        // val scope = CoroutineScope(Dispatchers.Main)
        // val scope = MainScope() // mesma coisa que o comando acima
        // um escopo pode ser alterado posteriormente, através do comando withContext
        // Por ex.: withContext(Dispatchers.IO)


        // com o launch, as execuções são em paralelo (coroutines) dentro do scope definido.

        // lifecycleScope não precisa ser criado, pois faz parte de biblioteca
        // lifecycleScope já lida com o cancelamento de coroutines de acordo com o fim da activity
        // por padrão, o lifecycleScope é na Main thread
        lifecycleScope.launch(handler) {
            // quando usamos Flow, o comando collect precisa estar sozinho em uma coroutine
            fluxoProdutos.collect {
                adapter.atualiza(it)
            }
        }
    }

    private fun configFAB() {
        val fab = binding.activityListaProdutosFab
        fab.setOnClickListener {
            chamaFormularioProduto()
        }
    }

    private fun chamaFormularioProduto() {
        val intent = Intent(this, FormProdActivity::class.java)
        startActivity(intent)
    }

    private fun configRecyclerView() {
        val recyclerView = binding.activityListaProdutosRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                ProdutoDetalhesActivity::class.java
            ).apply {
                putExtra(CHAVE_PRODUTO_ID, it.id)
            }
            startActivity(intent)
        }
    }

    private fun funcaoTesteRunBlocking() {
        // runBlocking permite que sejam disparadas coroutines dentro do seu escopo
        // este comando deve ser usado apenas em testes !
        // runBlocking trava a execução do programa até que todo seu código
        // seja executado
        runBlocking {
            Log.i(TAG, "onCreate: runBlocking inicio")
            // o comando launch consegue disparar uma coroutine
            // para uso eficaz de coroutines, o código não pode causar travamento de thread
            launch {
                Log.i(TAG, "onCreate: launch inicio ")
                delay(10000)
                //Thread.sleep(5000) <<< este comando causa travamento de thread
                Log.i(TAG, "onCreate: launch fim ")
            }
            Log.i(TAG, "onCreate: runBlocking fim")
        }
    }

    private fun funcaoTesteJob() {
        val scope = MainScope()
        val job = Job()
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            // aqui vai o código equivalente ao catch
        }
        scope.launch(job) {
            repeat(1000) {
                Log.i(TAG, "onCreate: teste de cancelamento de job")
                delay(1000)
                job.cancel()
            }
        }
        val jobPrimario = scope.launch(job + handler + Dispatchers.IO + CoroutineName("primaria")) {
            repeat(1000) {
                Log.i(TAG, "onCreate: teste de cancelamento de job")
                delay(1000)
            }
        }
        jobPrimario.cancel()
    }

}