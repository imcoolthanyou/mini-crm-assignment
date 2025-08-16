// lib/models/order.dart
class Order {
  final int id;
  final int customerId;
  final String orderTitle;
  final int orderAmount;
  final int orderDate;

  Order({
    required this.id,
    required this.customerId,
    required this.orderTitle,
    required this.orderAmount,
    required this.orderDate,
  });

  // The factory constructor - this looks correct
  factory Order.fromMap(Map<dynamic, dynamic> map) {
    // No use of 'this.' before returning a new instance
    return Order(
      id: map['id'] as int? ?? 0,
      customerId: map['customerId'] as int? ?? 0,
      orderTitle: map['orderTitle'] as String? ?? '',
      orderAmount: map['orderAmount'] as int? ?? 0,
      orderDate: map['orderDate'] as int? ?? 0,
    );
  }

  Map<String, dynamic> toMap() {
    // 'toMap' correctly uses 'this.'
    return {
      'id': this.id,
      'customerId': this.customerId,
      'orderTitle': this.orderTitle,
      'orderAmount': this.orderAmount,
      'orderDate': this.orderDate,
    };
  }

  @override
  String toString() {
    // 'toString' correctly uses 'this.'
    return 'Order(id: ${this.id}, customerId: ${this.customerId}, ...)'; // 'this.' is optional
  }
}