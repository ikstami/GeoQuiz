// MainActivity.kt
package com.example.geoquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class
data class Question(
    val text: String,
    val answer: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeoQuizApp()
                }
            }
        }
    }
}

@Composable
fun GeoQuizApp() {
    // Состояние приложения
    var currentIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf<List<Boolean?>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }
    var correctAnswersCount by remember { mutableStateOf(0) }

    val questions = listOf(
        Question("Canberra is the capital of Australia.", true),
        Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
        Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
        Question("The source of the Nile River is in Egypt.", false),
        Question("The Amazon River is the longest river in the Americas.", true),
        Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
    )

    // Инициализация ответов пользователя
    if (userAnswers.isEmpty()) {
        userAnswers = List(questions.size) { null }
    }

    val currentQuestion = questions[currentIndex]
    val isLastQuestion = currentIndex == questions.size - 1
    val currentAnswer = userAnswers[currentIndex]
    val isAnswered = currentAnswer != null

    // Функции
    fun answerQuestion(answer: Boolean) {
        val updatedAnswers = userAnswers.toMutableList()
        updatedAnswers[currentIndex] = answer
        userAnswers = updatedAnswers

        // Проверка ответа и подсчет правильных
        val isCorrect = answer == currentQuestion.answer
        if (isCorrect) {
            correctAnswersCount++
        }

        // Если это последний вопрос, показываем результаты
        if (isLastQuestion) {
            showResult = true
        }
    }

    fun moveToNext() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
        }
    }

    fun calculateScore(): Int {
        return questions.indices.count { index ->
            userAnswers[index] == questions[index].answer
        }
    }

    fun resetQuiz() {
        currentIndex = 0
        userAnswers = List(questions.size) { null }
        showResult = false
        correctAnswersCount = 0
    }

    // Дизайн как в оригинальном XML
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Question Text (аналог TextView)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = currentQuestion.text,
                modifier = Modifier.padding(24.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // True/False Buttons Row
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TRUE Button
            Button(
                onClick = { answerQuestion(true) },
                enabled = !isAnswered,
                modifier = Modifier.width(120.dp)
            ) {
                Text("TRUE")
            }

            Spacer(modifier = Modifier.width(16.dp))

            // FALSE Button
            Button(
                onClick = { answerQuestion(false) },
                enabled = !isAnswered,
                modifier = Modifier.width(120.dp)
            ) {
                Text("FALSE")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // NEXT Button (скрывается на последнем вопросе)
        if (!isLastQuestion) {
            Button(
                onClick = { moveToNext() },
                enabled = isAnswered,
                modifier = Modifier.width(120.dp)
            ) {
                Text("NEXT")
            }
        }
    }

    // Final Results Dialog (после ответа на последний вопрос)
    if (showResult) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss */ },
            title = {
                Text(
                    text = "Quiz Completed!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                val score = calculateScore()
                val totalQuestions = questions.size
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your final score:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$score/$totalQuestions",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            score == totalQuestions -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            score >= totalQuestions / 2 -> androidx.compose.ui.graphics.Color(0xFF2196F3)
                            else -> androidx.compose.ui.graphics.Color(0xFFF44336)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            },
            confirmButton = {
                Button(
                    onClick = { resetQuiz() }
                ) {
                    Text("Play Again")
                }
            }
        )
    }
}