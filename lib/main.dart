import 'package:flutter/material.dart';
import 'package:vibration/vibration.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData.dark(useMaterial3: true),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _isVibrating = false;

  void _startVibration() async {
    if (await Vibration.hasVibrator()) {
      Vibration.vibrate(pattern: [0, 15000, 500], repeat: 0);
      setState(() {
        _isVibrating = true;
      });
    }
  }

  void _stopVibration() {
    Vibration.cancel();
    setState(() {
      _isVibrating = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Vibration Control')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: _isVibrating ? null : _startVibration, // Disable if already vibrating
              child: const Text('Start Vibration'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _isVibrating ? _stopVibration : null, // Disable if not vibrating
              child: const Text('Stop Vibration'),
            ),
          ],
        ),
      ),
    );
  }
}
