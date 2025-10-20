package ipca.example.calculator

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ipca.example.calculator.ui.theme.CalculatorTheme
import ipca.example.calculator.ui.theme.Orange
import ipca.example.calculator.ui.theme.Purple40

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    label: String,
    isOperation: Boolean = false,
    onNumPressed: (String) -> Unit
){
    Button(
        modifier = modifier
            .size(100.dp)
            .aspectRatio(1f)
            .padding(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOperation) Orange else Purple40
        ),
        onClick = {
            onNumPressed(label)
        }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorButtonPreview(){
    CalculatorTheme {
        CalculatorButton(
            modifier = Modifier,
            label = "⌫",
            isOperation = true,
            onNumPressed = {}
        )
    }
}