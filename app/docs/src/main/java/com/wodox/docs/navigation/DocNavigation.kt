//package com.wodox.docs.navigation
//
//
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.compose.composable
//import com.wodox.docs.ui.DocScreenRoute
//
//const val docScreenRoute = "docScreenRoute"
//
//fun NavController.navigateToDocScreen() {
//    navigate(docScreenRoute)
//}
//
//fun NavGraphBuilder.docScreen(
//    onBackBtnClick: () -> Unit
//) {
//    composable(route = docScreenRoute) {
//        DocScreenRoute(
//            onBackBtnClick = onBackBtnClick
//        )
//    }
//}