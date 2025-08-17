package eu.tutorials.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.unitconverter.ui.theme.UnitConverterTheme
import kotlin.math.roundToInt
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnitConverterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnitConverterApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ Unit Converter") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            UnitConverter()
        }
    }
}

@Composable
fun UnitConverter() {
    // Categories and units (all in one place)
    val unitCategories = mapOf(
        "Length" to mapOf(
            "Millimeters" to 0.001,
            "Centimeters" to 0.01,
            "Meters" to 1.0,
            "Kilometers" to 1000.0,
            "Inches" to 0.0254,
            "Feet" to 0.3048,
            "Yards" to 0.9144,
            "Miles" to 1609.34
        ),
        "Weight" to mapOf(
            "Grams" to 1.0,
            "Kilograms" to 1000.0,
            "Pounds" to 453.592,
            "Ounces" to 28.3495
        ),
        "Temperature" to mapOf( // handled differently
            "Celsius" to 1.0,
            "Fahrenheit" to 1.0,
            "Kelvin" to 1.0
        )
    )

    var category by remember { mutableStateOf("Length") }
    var inputValue by remember { mutableStateOf("") }
    var outputValue by remember { mutableStateOf("") }
    var inputUnit by remember { mutableStateOf("Meters") }
    var outputUnit by remember { mutableStateOf("Meters") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var iExpanded by remember { mutableStateOf(false) }
    var oExpanded by remember { mutableStateOf(false) }

    fun convertUnits() {
        val inputDouble = inputValue.toDoubleOrNull() ?: return run { outputValue = "" }

        if (category == "Temperature") {
            outputValue = when (inputUnit to outputUnit) {
                "Celsius" to "Fahrenheit" -> (inputDouble * 9 / 5 + 32).roundTo(2).toString()
                "Celsius" to "Kelvin" -> (inputDouble + 273.15).roundTo(2).toString()
                "Fahrenheit" to "Celsius" -> ((inputDouble - 32) * 5 / 9).roundTo(2).toString()
                "Fahrenheit" to "Kelvin" -> ((inputDouble - 32) * 5 / 9 + 273.15).roundTo(2).toString()
                "Kelvin" to "Celsius" -> (inputDouble - 273.15).roundTo(2).toString()
                "Kelvin" to "Fahrenheit" -> ((inputDouble - 273.15) * 9 / 5 + 32).roundTo(2).toString()
                else -> inputDouble.toString()
            }
        } else {
            val inputFactor = unitCategories[category]?.get(inputUnit) ?: 1.0
            val outputFactor = unitCategories[category]?.get(outputUnit) ?: 1.0
            val result = (inputDouble * inputFactor / outputFactor).roundTo(2)
            outputValue = result.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Category Selector
        UnitDropdown(
            selectedUnit = category,
            expanded = categoryExpanded,
            onExpand = { categoryExpanded = true },
            onDismiss = { categoryExpanded = false },
            units = unitCategories.keys.toList(),
            onUnitSelected = {
                category = it
                inputUnit = unitCategories[category]!!.keys.first()
                outputUnit = unitCategories[category]!!.keys.first()
                categoryExpanded = false
                convertUnits()
            },
            label = "Select Category"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Input Field
        OutlinedTextField(
            value = inputValue,
            onValueChange = {
                inputValue = it
                convertUnits()
            },
            label = { Text("Enter Value") },
            trailingIcon = {
                if (inputValue.isNotEmpty()) {
                    IconButton(onClick = {
                        inputValue = ""
                        outputValue = ""
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Unit Selectors
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            UnitDropdown(
                selectedUnit = inputUnit,
                expanded = iExpanded,
                onExpand = { iExpanded = true },
                onDismiss = { iExpanded = false },
                units = unitCategories[category]!!.keys.toList(),
                onUnitSelected = {
                    inputUnit = it
                    iExpanded = false
                    convertUnits()
                },
                label = "From"
            )

            Spacer(modifier = Modifier.width(20.dp))

            UnitDropdown(
                selectedUnit = outputUnit,
                expanded = oExpanded,
                onExpand = { oExpanded = true },
                onDismiss = { oExpanded = false },
                units = unitCategories[category]!!.keys.toList(),
                onUnitSelected = {
                    outputUnit = it
                    oExpanded = false
                    convertUnits()
                },
                label = "To"
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Converted Value", color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (outputValue.isNotEmpty()) "$outputValue $outputUnit" else "--",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun UnitDropdown(
    selectedUnit: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    units: List<String>,
    onUnitSelected: (String) -> Unit,
    label: String = ""
) {
    Column {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Box {
            Button(
                onClick = onExpand,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(selectedUnit)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = { onUnitSelected(unit) }
                    )
                }
            }
        }
    }
}

// Helper function for rounding
fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}

@Preview(showBackground = true)
@Composable
fun PreviewConverter() {
    UnitConverterTheme {
        UnitConverterApp()
    }
}
