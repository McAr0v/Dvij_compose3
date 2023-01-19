package kz.dvij.dvij_compose3.photohelper

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun chooseImageDesign (act: MainActivity): Uri? {

    var selectImage = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
        selectImage.value = it
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(15.dp),
        backgroundColor = Grey100
    ) {

        if (selectImage.value == null) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            galleryLauncher.launch("image/*")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "",
                        tint = Grey10
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Добавь изображение",
                        color = Grey10,
                        style = Typography.bodyMedium
                    )

                }
            }

        } else {

            Image(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color = Grey95
                    )
                    .fillMaxWidth()
                    .fillMaxHeight(),
                painter = (
                        if (selectImage.value == null) {
                            painterResource(id = R.drawable.korn_concert)
                        } else {
                            rememberAsyncImagePainter(model = selectImage.value)
                        }
                        ),
                contentDescription = "",

                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.background(WarningColor, shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "",
                        tint = Grey95
                    )
                }

                IconButton(
                    onClick = { selectImage.value = null },
                    modifier = Modifier.background(AttentionColor, shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "",
                        tint = Grey95
                    )
                }
            }
        }
    }

    return selectImage.value

}