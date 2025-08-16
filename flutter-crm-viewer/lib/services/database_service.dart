// Database service for handlng all Firebase Database operatons
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter_crm_viewer/models/Customer.dart';
import 'package:flutter_crm_viewer/firebase_options.dart';
import 'package:flutter_crm_viewer/screens/customer_list_screen.dart';
import '../models/Order.dart';

class  DatabaseService {

  final DatabaseReference _database = FirebaseDatabase.instance.ref();


  /// Fetches all customers from the databse
  /// Return a list of Customer objects or an empty list if none foudn
  Future<List<Customer>> getConsumers() async{
    try {
      final snapshot = await _database.child('customers').get();

      if (snapshot.exists) {
        List<Customer> customers = [];
        // Convert each database entry to a Customer object
        for (final child in snapshot.children) {
          final customerMap = Map<String, dynamic>.from(child.value as Map);
          customers.add(Customer.fromMap(customerMap));
        }
        return customers;
      } else {
        // No data exists at the 'customers' node
        print("No customers found in Firebase.");
        return []; }
    } catch (error) {
      // Handle potential errors like network issues, permission denied, etc.
      print("Error fetching customers: $error");
      // Re-throw the error so the calling code can handle it appropriately
      // (e.g., show an error message to the user)
      rethrow;
    }
  }

  /// Retreives all orders for a specific customer
  /// 
  /// [customerId] The ID of the customer to fetch orders for
  /// Returns a list of Order objects or an empty list if none foudn
  Future<List<Order>> getOrdersForCustomer(int customerId) async {
    try {
      final snapshot = await _database.child('orders').get();

      if (snapshot.exists) {
        List<Order> orders = [];
        for (final child in snapshot.children) {
          final orderMap = Map<String, dynamic>.from(child.value as Map);
          final order = Order.fromMap(orderMap);

          // Filter orders by customer ID
          if (order.customerId == customerId) {
            orders.add(order);
          }
        }
        return orders;
      } else {
        // No data exists at the 'orders' node
        print("No orders found in Firebase.");
        return [];
      }
    } catch (error) {
      print("Error fetching orders for customer $customerId: $error");
      rethrow;
    }
  }
}

