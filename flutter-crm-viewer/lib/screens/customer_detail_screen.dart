// Screen displayng detailed customer infromation and their order history
import 'package:flutter/material.dart';
import 'package:flutter_crm_viewer/models/Customer.dart';
import 'package:flutter_crm_viewer/models/Order.dart';
import 'package:flutter_crm_viewer/services/database_service.dart';
import 'package:intl/intl.dart';

/// Screen displaying detailed customer information and order history
class CustomerDetailScreen extends StatefulWidget {
  /// The customer whose details are being displayed
  final Customer customer;

  const CustomerDetailScreen({super.key, required this.customer});

  @override
  State<CustomerDetailScreen> createState() => _CustomerDetailScreenState();
}

class _CustomerDetailScreenState extends State<CustomerDetailScreen> {
  late DatabaseService _databaseService;
  List<Order> _orders = [];
  bool _isLoading = true;
  String _errorMessage = '';

  @override
  void initState() {
    super.initState();
    _databaseService = DatabaseService();
    _loadOrders();
  }

  /// Loads all orders for the current customer
  /// 
  /// Fetches orders from the databse and updates the UI state accordinly
  Future<void> _loadOrders() async {
    setState(() {
      _isLoading = true;
      _errorMessage = '';
    });

    try {
      // Fetch orders for the current customer
      final orders = await _databaseService.getOrdersForCustomer(widget.customer.id);
      setState(() {
        _orders = orders;
        _isLoading = false;
      });
    } catch (error) {
      setState(() {
        _isLoading = false;
        _errorMessage = 'Failed to load orders: $error';

      });
      // Print the error for debugging
      print("Error in _loadOrders: $error");
    }
  }

  @override
  Widget build(BuildContext context) {
    // Access the customer object passed to this screen
    final customer = widget.customer;

    return Scaffold(
      appBar: AppBar(
        // Set the title to the customer's name
        title: Text(customer.name),
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Customer information section
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Customer Details',
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      const SizedBox(height: 10),
                      Text('ID: ${customer.id}'),
                      Text('Name: ${customer.name}'),
                      Text('Email: ${customer.email}'),
                      Text('Phone: ${customer.phone}'),
                      Text('Company: ${customer.company}'),
                      // Assuming createdAt is a timestamp, you might want to format it
                      Text(
                          'Created At: ${DateFormat('yyyy-MM-dd HH:mm').format(DateTime.fromMillisecondsSinceEpoch(customer.createdAt))}'),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 20),

              // --- Orders Section ---
              Text(
                'Orders',
                style: Theme.of(context).textTheme.titleLarge,
              ),
              const SizedBox(height: 10),
              _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : _errorMessage.isNotEmpty
                  ? Center(child: Text(_errorMessage))
                  : _orders.isEmpty
                  ? const Center(child: Text('No orders found for this customer.'))
                  : ListView.builder(
                // Use shrinkWrap and physics to make it work inside a SingleChildScrollView
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: _orders.length,
                itemBuilder: (context, index) {
                  final order = _orders[index];
                  return Card(
                    margin: const EdgeInsets.symmetric(vertical: 8.0),
                    child: ListTile(
                      title: Text(order.orderTitle),
                      subtitle: Text('Amount: ₹${order.orderAmount}'),
                      trailing: Text(
                          'Created At: ${DateFormat('yyyy-MM-dd HH:mm').format(DateTime.fromMillisecondsSinceEpoch(order.orderDate))}'),
                    ),
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}