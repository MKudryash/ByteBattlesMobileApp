import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.R
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString

@Composable
fun CodeEditor(
    initialCode: String = "using System;\n\nnamespace Solution\n{\n    public static class Program\n    {\n        public static double SUM(int a, int b)\n        {\n            return 0;\n        }\n    }\n}",
    onCodeChange: (String) -> Unit = {},
    language: CodeLang
) {


    val parser = remember { PrettifyParser() }
    val themeState by remember { mutableStateOf(CodeThemeType.Default) }
    val theme = remember(themeState) { themeState.theme() }

    fun parse(code: String): AnnotatedString {
        return parseCodeAsAnnotatedString(
            parser = parser,
            theme = theme,
            lang = language,
            code = code
        )
    }
    var code by remember { mutableStateOf(initialCode) }

    var textFieldValue by remember { mutableStateOf(TextFieldValue(parse(code))) }
    var lineTops by remember { mutableStateOf(emptyArray<Float>()) }
    val density = LocalDensity.current

    val textStyle = TextStyle(
        color = Color(0xFFFFFFFF),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
        lineHeight = 20.sp
    )

    val lineHeight = 20.sp
    val lines = code.split("\n").size
    val minHeight = (lines * lineHeight.value).dp + 32.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Внешний Box с разделителем
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Обертка для номеров строк с линией
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            val lineList = code.split("\n")
                            lineTops.forEachIndexed { index, _ ->
                                Text(
                                    text = "${index + 1}",
                                    color = Color(0xFF858585),
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }

                        // Вертикальная линия справа
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .align(Alignment.CenterEnd)
                                .background(Color(0xFF53C2C3))
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Поле для ввода
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = {
                                textFieldValue =  it.copy(annotatedString = parse(it.text))
                                onCodeChange(it.text)
                            },
                            textStyle = textStyle,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            cursorBrush = SolidColor(Color.White),
                            onTextLayout = { result ->
                                lineTops = Array(result.lineCount) { result.getLineTop(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CodeEditorPreview() {
    CodeEditor(language = CodeLang.CSharp)
}