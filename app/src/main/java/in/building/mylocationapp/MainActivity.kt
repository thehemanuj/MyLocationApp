package `in`.building.mylocationapp

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.building.mylocationapp.ui.theme.MyLocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel:LocationViewModel = viewModel()
            MyLocationAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MyApp(
                            modifier=Modifier.padding(innerPadding),
                            viewModel
                        )
                }
            }
        }
    }
}



@Composable
fun MyApp(modifier:Modifier,viewModel:LocationViewModel){
    val context=LocalContext.current
    val locationUtils=LocationUtils(context)
    LocationDisplay(locationUtils,context,viewModel)
}






@Composable
fun LocationDisplay(locationUtils:LocationUtils,
                    context: Context,
                    viewModel:LocationViewModel){


    val location=viewModel.location.value

    val requestPermissionLauncher=rememberLauncherForActivityResult(
        contract=ActivityResultContracts.RequestMultiplePermissions(),
        onResult={permissions->
            if(permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true
                &&permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true){

                locationUtils.requestLocationUpdates(viewModel=viewModel)

            }
            else{
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                if(rationaleRequired){
                    Toast.makeText(context,"Location Permission Is Required😗",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Please enable the location from the settings😭",Toast.LENGTH_LONG).show()
                }
            }
        }
    )




val address=location?.let{
    locationUtils.reverseGeocodeLocation(location)
}




Column(modifier=Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally){
    if(location !=null){
        Text("Latitude:${location.latitude}")
        Text("Longitude:${location.longitude}")
        Text("$address",textAlign= TextAlign.Center)
    }
    else
        Text("Allow Location Access🤌🏻")
    Button(onClick = {
        if(locationUtils.hasLocationPermission(context)){
            //permission granted update location
            locationUtils.requestLocationUpdates(viewModel)
        }
        else{
            // request permission
         requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
        }
    }){
        Text("Get Location")
    }
}
}