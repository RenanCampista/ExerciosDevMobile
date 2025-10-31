package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            try {
                val salario = binding.editSalario.text.toString().toFloat()
                val gastos = binding.editGastos.text.toString().toFloat()
                val dependentes = binding.editDependentes.text.toString().toInt()

                val baseCalculo = salario - gastos - (dependentes * 189.59f)
                val imposto = if (baseCalculo > 2000) baseCalculo * 0.15f else 0f

                binding.textResultado.text = "SEU IMPOSTO DE RENDA MENSAL:\nR$ %.2f".format(imposto)
                Toast.makeText(this, "Cálculo realizado com sucesso!", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
