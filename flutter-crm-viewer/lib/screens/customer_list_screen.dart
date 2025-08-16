// Screen displayng the list of all customers
import 'package:flutter/material.dart';
import 'package:flutter_crm_viewer/models/Customer.dart';
import 'package:flutter_crm_viewer/services/database_service.dart';
import 'customer_detail_screen.dart';

class CustomerListScreen extends StatefulWidget {
  const CustomerListScreen({super.key});

  @override
  State<CustomerListScreen> createState() => _CustomerListScreenState();
}

class _CustomerListScreenState extends State<CustomerListScreen> {
  late DatabaseService _databaseService;
  List<Customer> _customers = [];
  bool _isLoading = true;
  String _errorMessage = '';

  @override
  void initState() {
    super.initState();
    _databaseService = DatabaseService();
    _loadCustomers();
  }

  /// Fetches all customers from the databse
  /// 
  /// Updates the UI state with the fethced customers or an error message
  Future<void> _loadCustomers() async {
    setState(() {
      _isLoading = true;
      _errorMessage = '';
    });

    try {
      final customers = await _databaseService.getConsumers();
      setState(() {
        _customers = customers;
        _isLoading = false;
      });
    } catch (error) {
      setState(() {
        _isLoading = false;
        _errorMessage = 'Failed to load customers: $error';

      });
      // Log error for debugging purposes
      print("Error in _loadCustomers: $error");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Customers'),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage.isNotEmpty
          ? Center(child: Text(_errorMessage))
          : _customers.isEmpty
          ? const Center(child: Text('No customers found.'))
          : ListView.builder(
        itemCount: _customers.length,
        itemBuilder: (context, index) {
          final customer = _customers[index];
          return ListTile(
            title: Text(customer.name),
            subtitle: Text(customer.company),
            // Navigate to CustomerDetailScreen when tapped
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) =>
                      CustomerDetailScreen(customer: customer),
                ),
              );
            },
          );
        },
      ),
    );
  }
}