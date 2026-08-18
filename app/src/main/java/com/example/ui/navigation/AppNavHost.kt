package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.model.UserRole
import com.example.data.repository.PodoRepository
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.EDevletAuthScreen
import com.example.ui.screens.auth.RoleSelectScreen
import com.example.ui.screens.chat.AiPodologyBotScreen
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.diabetic.DiabeticFootScreen
import com.example.ui.screens.doctor.DoctorDashboardScreen
import com.example.ui.screens.doctor.DoctorDetailScreen
import com.example.ui.screens.doctor.PendingApprovalScreen
import com.example.ui.screens.education.EducationScreen
import com.example.ui.screens.home.PatientHomeScreen
import com.example.ui.screens.map.MapScreen
import com.example.ui.screens.profile.EditProfileScreen
import com.example.ui.screens.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: PodoRepository,
    modifier: Modifier = Modifier
) {
    val currentRole by repository.currentRole.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route,
        modifier = modifier
    ) {
        composable(Screen.RoleSelect.route) {
            RoleSelectScreen(
                repository = repository,
                onNavigateToPatientHome = {
                    navController.navigate(Screen.PatientHome.route) {
                        popUpTo(Screen.RoleSelect.route) { inclusive = true }
                    }
                },
                onNavigateToDoctorDashboard = {
                    navController.navigate(Screen.DoctorDashboard.route) {
                        popUpTo(Screen.RoleSelect.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                repository = repository,
                onNavigateToPatientHome = {
                    navController.navigate(Screen.PatientHome.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToDoctorDashboard = {
                    navController.navigate(Screen.DoctorDashboard.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToEDevlet = {
                    navController.navigate(Screen.EDevletAuth.route)
                }
            )
        }

        composable(Screen.EDevletAuth.route) {
            EDevletAuthScreen(
                repository = repository,
                onVerificationSuccess = {
                    navController.navigate(Screen.DoctorDashboard.route) {
                        popUpTo(Screen.EDevletAuth.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PendingApproval.route) {
            PendingApprovalScreen(
                repository = repository,
                onNavigateToEDevlet = {
                    navController.navigate(Screen.EDevletAuth.route)
                },
                onSwitchToPatient = {
                    repository.setRole(UserRole.PATIENT)
                    navController.navigate(Screen.PatientHome.route) {
                        popUpTo(Screen.PendingApproval.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PatientHome.route) {
            PatientHomeScreen(
                repository = repository,
                onNavigateToDoctor = {
                    navController.navigate(Screen.DoctorDetail.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.DoctorChat.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.Education.route)
                },
                onNavigateToDiabeticFoot = {
                    navController.navigate(Screen.DiabeticFoot.route)
                },
                onNavigateToAiBot = {
                    navController.navigate(Screen.AiBot.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onSwitchRole = {
                    repository.setRole(UserRole.PODOLOGIST)
                    navController.navigate(Screen.DoctorDashboard.route) {
                        popUpTo(Screen.PatientHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DoctorDashboard.route) {
            DoctorDashboardScreen(
                repository = repository,
                onSwitchRole = {
                    repository.setRole(UserRole.PATIENT)
                    navController.navigate(Screen.PatientHome.route) {
                        popUpTo(Screen.DoctorDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToEDevlet = {
                    navController.navigate(Screen.EDevletAuth.route)
                }
            )
        }

        composable(Screen.DoctorDetail.route) {
            DoctorDetailScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = {
                    navController.navigate(Screen.DoctorChat.route)
                }
            )
        }

        composable(Screen.DoctorChat.route) {
            ChatScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AiBot.route) {
            AiPodologyBotScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Education.route) {
            EducationScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.DiabeticFoot.route) {
            DiabeticFootScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                repository = repository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
