package com.renan.atividade1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.renan.atividade1.databinding.ActivityMainBinding

const val DEDUCAO_MENSAL_POR_DEPENDENTE = 189.59
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            try {
                val salario = binding.editSalario.text.toString().toFloat()
                val gastos = binding.editGastos.text.toString().toFloat()
                val dependentes = binding.editDependentes.text.toString().toInt()

                require(salario > 0) { "O valor do salário deve ser maior que zero!" }
                require(gastos >= 0) { "O valor dos gastos deve ser maior ou igual a zero!" }
                require(dependentes >= 0) { "O valor de dependentes deve ser maior ou igual a zero!" }

                val baseCalculo = salario - (dependentes * DEDUCAO_MENSAL_POR_DEPENDENTE) - gastos

                val iRMensal = when {
                    baseCalculo in 2428.81 .. 2826.65 -> (baseCalculo * 0.075) - 182.16
                    baseCalculo in 2826.66 .. 3751.05 -> (baseCalculo * 0.15) - 394.16
                    baseCalculo in 3751.06 .. 4664.68 -> (baseCalculo * 0.225) - 675.49
                    baseCalculo > 4664.68 -> (baseCalculo * 0.275) - 908.73
                    else -> 0.00
                }
                binding.textResultado.text = "R$ %.2f".format(iRMensal)
                Toast.makeText(this, "Cálculo realizado com sucesso!", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}