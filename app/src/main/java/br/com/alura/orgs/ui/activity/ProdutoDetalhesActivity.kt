package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityProdutoDetalhesBinding
import br.com.alura.orgs.extensions.formataEmReais
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProdutoDetalhesActivity : AppCompatActivity() {

    private var produtoId: Long = 0L
    private var produto: Produto? = null

    private val binding by lazy {
        ActivityProdutoDetalhesBinding.inflate(layoutInflater)
    }

    private val produtoDAO by lazy {
        AppDatabase.instancia(this).produtoDAO()
    }

    // este objeto foi utilizado nos primeiros testes,
    // antes de usar lifecycleScope e room-ktx
    //private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        carregaProduto()
        title = APPBAR_DETALHES
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            produto = produtoDAO.buscaPorId(produtoId)
            // códigos que alteram a tela devem ser sempre executados na Main thread
                produto?.let {
                    preencheCampos(it)
                } ?: finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.produto_detalhes_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_editar -> {
                Intent(this, FormProdActivity::class.java).apply {
                    putExtra(CHAVE_PRODUTO_ID, produtoId)
                    startActivity(this)
                }
            }
            R.id.menu_item_remover -> {
                lifecycleScope.launch {
                    produto?.let { produtoDAO.remove(it) }
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun carregaProduto() {
        produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
    }

    private fun preencheCampos(produtoCarregado: Produto) {
        with(binding) {
            activityDetalhesNome.setText(produtoCarregado.nome)
            activityDetalhesDescricao.setText(produtoCarregado.descricao)
            activityDetalhesValor.setText(produtoCarregado.valor.formataEmReais())
            activityDetalhesImagem.tentaCarregarImagem(produtoCarregado.imagem)
        }
    }
}
