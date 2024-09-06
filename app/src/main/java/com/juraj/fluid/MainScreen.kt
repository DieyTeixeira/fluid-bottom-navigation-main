package com.juraj.fluid // Define o pacote onde esse arquivo está localizado.

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juraj.fluid.ui.theme.DEFAULT_PADDING
import com.juraj.fluid.ui.theme.FluidBottomNavigationTheme
import kotlin.math.PI
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.S) // Essa função só pode ser usada em dispositivos com Android S ou superior.
private fun getRenderEffect(): RenderEffect { // Define um efeito de renderização.

    val blurEffect = RenderEffect
        .createBlurEffect(80f, 80f, Shader.TileMode.MIRROR) // Cria um efeito de desfoque de 80 pixels nos eixos X e Y.

    val alphaMatrix = RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 0f, // Canal de vermelho inalterado.
                    0f, 1f, 0f, 0f, 0f, // Canal de verde inalterado.
                    0f, 0f, 1f, 0f, 0f, // Canal de azul inalterado.
                    0f, 0f, 0f, 50f, -5000f // Manipula o canal alfa (transparência).
                )
            )
        )
    )

    return RenderEffect
        .createChainEffect(alphaMatrix, blurEffect) // Combina os efeitos de desfoque e de filtro de cores.
}


@Composable // Declara que essa função é uma função de interface de usuário (UI).
fun MainScreen() {
    val isMenuExtended = remember { mutableStateOf(false) } // Armazena o estado se o menu de FABs está aberto (true) ou fechado (false).

    val fabAnimationProgress by animateFloatAsState( // Controla o progresso da animação do FAB.
        targetValue = if (isMenuExtended.value) 1f else 0f, // Define o valor de destino da animação com base se o menu está aberto ou não.
        animationSpec = tween( // Define as especificações da animação.
            durationMillis = 1000, // A animação dura 1 segundo.
            easing = LinearEasing // Usa uma animação linear (constante).
        )
    )

    val clickAnimationProgress by animateFloatAsState( // Anima o progresso do clique no FAB.
        targetValue = if (isMenuExtended.value) 1f else 0f, // Valor de destino para o progresso da animação de clique.
        animationSpec = tween( // Define as especificações de tempo.
            durationMillis = 400, // A animação dura 0,4 segundos.
            easing = LinearEasing // Anima linearmente.
        )
    )

    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Verifica se a versão do Android suporta RenderEffects.
        getRenderEffect().asComposeRenderEffect() // Aplica o efeito de renderização caso a versão suporte.
    } else {
        null // Caso contrário, não aplica nenhum efeito.
    }

    MainScreen( // Chama a função que constrói a tela principal.
        renderEffect = renderEffect,
        fabAnimationProgress = fabAnimationProgress,
        clickAnimationProgress = clickAnimationProgress
    ) {
        isMenuExtended.value = isMenuExtended.value.not() // Alterna o estado do menu ao clicar.
    }
}

@Composable
fun MainScreen(
    renderEffect: androidx.compose.ui.graphics.RenderEffect?, // O efeito de renderização aplicado.
    fabAnimationProgress: Float = 0f, // Progresso da animação do FAB.
    clickAnimationProgress: Float = 0f, // Progresso da animação de clique.
    toggleAnimation: () -> Unit = { } // Função que alterna a animação.
) {
    Box(
        Modifier
            .fillMaxSize() // Preenche o máximo de espaço disponível.
            .padding(bottom = 24.dp), // Adiciona padding na parte inferior.
        contentAlignment = Alignment.BottomCenter // Alinha o conteúdo ao centro na parte inferior.
    ) {
        CustomBottomNavigation() // Chama a função que cria a barra de navegação inferior.
        Diamond( // Desenha um círculo animado.
            color = MaterialTheme.colors.primary.copy(alpha = 0.5f), // Define a cor primária com opacidade reduzida.
            animationProgress = 0.5f // Define o progresso da animação.
        )

        FabGroup(renderEffect = renderEffect, animationProgress = fabAnimationProgress) // Desenha um grupo de FABs com o efeito de renderização.
        FabGroup( // Desenha outro grupo de FABs, sem o efeito de renderização.
            renderEffect = null,
            animationProgress = fabAnimationProgress,
            toggleAnimation = toggleAnimation
        )
        Diamond( // Desenha outro círculo animado.
            color = Color.White,
            animationProgress = clickAnimationProgress
        )
    }
}

@Composable
fun Circle(color: Color, animationProgress: Float) { // Função que desenha um círculo animado.
    val animationValue = sin(PI * animationProgress).toFloat() // Calcula o valor da animação com base no seno.

    Box(
        modifier = Modifier
            .padding(DEFAULT_PADDING.dp) // Aplica padding ao redor do círculo.
            .size(56.dp) // Define o tamanho do círculo.
            .scale(2 - animationValue) // Escala o círculo de acordo com o progresso da animação.
            .border( // Desenha uma borda ao redor do círculo.
                width = 2.dp, // Define a largura da borda.
                color = color.copy(alpha = color.alpha * animationValue), // A cor da borda é definida pela opacidade do progresso.
                shape = CircleShape // Define a borda como circular.
            )
    )
}

@Composable
fun Diamond(color: Color, animationProgress: Float) {
    val animationValue = sin(PI * animationProgress).toFloat() // Calcula o valor da animação com base no seno.

    Box(
        modifier = Modifier
            .padding(DEFAULT_PADDING.dp) // Aplica padding ao redor do losango.
            .size(56.dp) // Define o tamanho do losango.
            .scale(2 - animationValue) // Escala o losango de acordo com o progresso da animação.
            .rotate(45f) // Rotaciona o quadrado 45 graus para criar o efeito de losango.
            .border( // Desenha uma borda ao redor do losango.
                width = 2.dp, // Define a largura da borda.
                color = color.copy(alpha = color.alpha * animationValue), // A cor da borda é definida pela opacidade do progresso.
                shape = RoundedCornerShape(0.dp, 10.dp, 0.dp, 10.dp) // Define o quadrado com cantos arredondados.
            )
    )
}

@Composable
fun CustomBottomNavigation() { // Função que desenha a barra de navegação inferior.
    Row (
        horizontalArrangement = Arrangement.SpaceBetween, // Distribui os elementos horizontalmente com espaço entre eles.
        verticalAlignment = Alignment.CenterVertically, // Alinha verticalmente ao centro.
        modifier = Modifier
            .height(80.dp) // Define a altura da barra de navegação.
            .size(100.dp)
            .rotate(45f)
            .background(color = Color(0xFF3A2F6E))
    ) {}

    Row(
        horizontalArrangement = Arrangement.SpaceBetween, // Distribui os elementos horizontalmente com espaço entre eles.
        verticalAlignment = Alignment.CenterVertically, // Alinha verticalmente ao centro.
        modifier = Modifier
            .height(80.dp) // Define a altura da barra de navegação.
            .paint( // Aplica uma imagem de fundo à barra.
                painter = painterResource(R.drawable.bottom_navigation), // Usa uma imagem do recurso `bottom_navigation`.
                contentScale = ContentScale.FillHeight // Ajusta a imagem para preencher a altura.
            )
            .padding(horizontal = 40.dp) // Aplica padding horizontal.
    ) {
        listOf(Icons.Filled.CalendarToday, Icons.Filled.Group).map { image -> // Itera sobre a lista de ícones para criar botões.
            IconButton(onClick = { }) { // Cria um botão de ícone.
                Icon(imageVector = image, contentDescription = null, tint = Color.White) // Define o ícone e sua cor.
            }
        }
    }
}

@Composable
fun FabGroup(
    animationProgress: Float = 0f, // Progresso da animação do grupo de FABs.
    renderEffect: androidx.compose.ui.graphics.RenderEffect? = null, // O efeito de renderização aplicado aos FABs.
    toggleAnimation: () -> Unit = { } // Função que alterna a animação dos FABs.
) {
    Box( // Cria uma caixa que engloba os FABs.
        Modifier
            .fillMaxSize() // A caixa ocupa todo o espaço disponível.
            .graphicsLayer { this.renderEffect = renderEffect } // Aplica o efeito de renderização (se existir) à caixa.
            .padding(bottom = DEFAULT_PADDING.dp), // Adiciona padding na parte inferior da caixa.
        contentAlignment = Alignment.BottomCenter // Alinha o conteúdo ao centro da parte inferior.
    ) {

        // Desenha o primeiro FAB animado
        AnimatedFab(
            icon = Icons.Default.PhotoCamera, // Ícone de câmera para o FAB.
            modifier = Modifier
                .padding(
                    PaddingValues(
                        bottom = 55.dp, // O FAB é posicionado 72 dp acima.
                        end = 120.dp // E 210 dp à direita.
                    ) * FastOutSlowInEasing.transform(0f, 0.8f, animationProgress) // A posição é animada com base no progresso.
                ),
            opacity = LinearEasing.transform(0.2f, 0.7f, animationProgress), // A opacidade do FAB também é animada.
            iconRotation = -45f
        )

        // Desenha o segundo FAB animado
        AnimatedFab(
            icon = Icons.Default.Settings, // Ícone de configurações.
            modifier = Modifier
                .padding(
                    PaddingValues(
                        bottom = 115.dp, // Posicionado 88 dp acima.
                    ) * FastOutSlowInEasing.transform(0.1f, 0.9f, animationProgress) // A posição é animada.
                ),
            opacity = LinearEasing.transform(0.3f, 0.8f, animationProgress), // A opacidade também é animada.
            iconRotation = -45f
        )

        // Desenha o terceiro FAB animado
        AnimatedFab(
            icon = Icons.Default.ShoppingCart, // Ícone de carrinho de compras.
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 55.dp, // Posicionado 72 dp acima.
                    start = 120.dp // E 210 dp à esquerda.
                ) * FastOutSlowInEasing.transform(0.2f, 1.0f, animationProgress) // A posição é animada.
            ),
            opacity = LinearEasing.transform(0.4f, 0.9f, animationProgress), // A opacidade é animada.
            iconRotation = -45f
        )

        // Desenha o FAB central animado (sem ícone)
        AnimatedFab(
            modifier = Modifier
                .scale(1f - LinearEasing.transform(0.5f, 0.85f, animationProgress)), // Anima a escala do FAB.
            iconRotation = -45f
        )

        // Desenha o FAB principal com o ícone de adicionar (+), que alterna a animação
        AnimatedFab(
            icon = Icons.Default.Add, // Ícone de adicionar.
            modifier = Modifier
                .rotate(
                    225 * FastOutSlowInEasing // Rotaciona o FAB com base na animação.
                        .transform(0.35f, 0.65f, animationProgress)
                ),
            onClick = toggleAnimation, // Ao clicar, a função `toggleAnimation` é chamada para alternar o menu.
            backgroundColor = Color.Transparent, // O fundo é transparente.
            iconRotation = -45f
        )
    }
}

@Composable
fun AnimatedFab( // Função que desenha um FAB animado.
    modifier: Modifier, // Modificador para posicionar e aplicar propriedades ao FAB.
    icon: ImageVector? = null, // Ícone opcional para o FAB.
    opacity: Float = 1f, // Define a opacidade do ícone.
    backgroundColor: Color = MaterialTheme.colors.secondary, // Cor de fundo do FAB.
    iconRotation: Float = 0f,
    onClick: () -> Unit = {} // Função que será chamada quando o FAB for clicado.
) {
    FloatingActionButton( // Componente padrão do Compose para FAB.
        onClick = onClick, // Chama a função ao clicar no FAB.
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp), // Define a elevação como 0 para todos os estados.
        backgroundColor = backgroundColor, // Define a cor de fundo do FAB.
        shape = RoundedCornerShape(0.dp, 10.dp, 0.dp, 10.dp),
        modifier = modifier
            .scale(1.10f) // Aplica uma escala de 1.25 ao FAB.
            .rotate(45f)
    ) {
        icon?.let { // Se um ícone for passado, ele é desenhado dentro do FAB.
            Icon(
                imageVector = it, // O ícone a ser desenhado.
                contentDescription = null, // Sem descrição de conteúdo.
                tint = Color.White.copy(alpha = opacity), // O ícone é desenhado com a opacidade configurada.
                modifier = Modifier.rotate(iconRotation)
            )
        }
    }
}

@Composable
@Preview(device = "id:pixel_4a", showBackground = true, backgroundColor = 0xFF3A2F6E) // Define uma prévia da tela para o dispositivo Pixel 4a.
private fun MainScreenPreview() {
    FluidBottomNavigationTheme { // Aplica o tema personalizado à prévia.
        MainScreen() // Chama a função MainScreen para exibir o conteúdo.
    }
}