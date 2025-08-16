// Main appliation entry point for the CRM Viewer
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_crm_viewer/firebase_options.dart';
import 'package:flutter_crm_viewer/screens/customer_list_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized(); // Ensure Flutter is ready
  // Initialize Firebase with the generated config
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'CRM Viewer',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const CustomerListScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}