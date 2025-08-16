// lib/models/customer.dart
class Customer {
  final int id;
  final String name;
  final String email;
  final String phone;
  final String company;
  final int createdAt;

  Customer({
    required this.id,
    required this.name,
    required this.email,
    required this.phone,
    required this.company,
    required this.createdAt,
  });

  // The factory constructor - this looks correct
  factory Customer.fromMap(Map<dynamic, dynamic> map) {
    // No use of 'this.' before returning a new instance
    return Customer(
      id: map['id'] as int? ?? 0,
      name: map['name'] as String? ?? '',
      email: map['email'] as String? ?? '',
      phone: map['phone'] as String? ?? '',
      company: map['company'] as String? ?? '',
      createdAt: map['createdAt'] as int? ?? 0, // <-- Check this line
    );
  }

  Map<String, dynamic> toMap() {

    return {
      'id': this.id,
      'name': this.name,
      'email': this.email,
      'phone': this.phone,
      'company': this.company,
      'createdAt': this.createdAt,
    };
  }

  @override
  String toString() {
    // 'toString' correctly uses 'this.'
    return 'Customer(id: ${this.id}, name: ${this.name}, ...)'; // 'this.' is optional
  }
}