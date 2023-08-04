package br.com.alura.orgs.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityFormProdBinding
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.ui.dialog.FormularioImagemDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class FormProdActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFormProdBinding.inflate(layoutInflater)
    }
    private var url: String? = null
    private var produtoId = 0L

    private val produtoDAO by lazy {
        val db = AppDatabase.instancia(this)
        db.produtoDAO()
    }

    // este objeto foi utilizado nos primeiros testes,
    // antes de usar lifecycleScope e room-ktx
//    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = APPBAR_CADASTRO
        configBotaoSalvar()
        binding.activityFormprodImagem.setOnClickListener {
            FormularioImagemDialog(this)
                .mostra(url) { imagem ->
                    url = imagem
                    binding.activityFormprodImagem.tentaCarregarImagem(url)
                }
        }
        carregaProdutoId()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            produtoDAO.buscaPorId(produtoId)?.let {
                // códigos que alteram a tela devem ser sempre executados na Main thread
                    preencheCampos(it)
            }
        }
    }

    private fun carregaProdutoId() {
        produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
    }

    private fun preencheCampos(produto: Produto) {

        binding.activityFormprodNome.setText(produto.nome)
        binding.activityFormprodDescricao.setText(produto.descricao)
        binding.activityFormprodValor.setText(produto.valor.toPlainString())
        binding.activityFormprodImagem.tentaCarregarImagem(produto.imagem)
        url = produto.imagem
    }

    private fun configBotaoSalvar() {

        val botaoSalvar = binding.activityFormprodBotaoSalvar

        //        botaoSalvar.setOnClickListener(object : View.OnClickListener {
        //            override fun onClick(v: View?) {
        //                val campoNome = findViewById<EditText>(R.id.nome)
        //                val nome = campoNome.text.toString()
        //                Log.i("FormProdActivity", "onCreate : nome = $nome")
        //            }
        //        })
        // aqui usando lambda
        botaoSalvar.setOnClickListener {
            val produtoNovo = criaProduto()

            // aqui neste código usamos chamadas separadas para INSERT e UPDATE
//            if (produtoId > 0) {
//                produtoDAO.atualiza(produtoNovo)
//            } else {
//                produtoDAO.salva(produtoNovo)
//                //Log.i("FormProdActivity", "onCreate : nome = ${dao.buscaTodos()}")
//            }

            // com INSERT OnConflictStrategy.REPLACE não precisa mais do UPDATE
            lifecycleScope.launch {
                produtoDAO.salva(produtoNovo)
                finish()
            }
        }
    }

    private fun criaProduto(): Produto {
        val campoNome = binding.activityFormprodNome
        val nome = campoNome.text.toString()
        val campoDescricao = binding.activityFormprodDescricao
        val descricao = campoDescricao.text.toString()
        val campoValor = binding.activityFormprodValor
        val valorEmTexto = campoValor.text.toString()


        // aqui uma if expression
        val valor = if (valorEmTexto.isBlank()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(valorEmTexto)
        }

        return Produto(
            id = produtoId,
            nome = nome,
            descricao = descricao,
            valor = valor,
            imagem = url
        )
    }

}