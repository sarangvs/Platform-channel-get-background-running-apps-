import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  static const backgroundAppsChannel = MethodChannel('bgChannel');

  Future<void> getBackgroundApps()async{
    try {
      var result = await backgroundAppsChannel.invokeMethod('bgApps');
      print(result);
    } catch (e) {
      print(e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Test App"),
      ),
      body:  Center(
        child: ElevatedButton(
          child: const Text("Get Background Apps"),
          onPressed:() {
            print("button clicked");
            getBackgroundApps();
            },
        ),
      ),
    );
  }
}
