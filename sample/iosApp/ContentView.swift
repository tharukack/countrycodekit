import SwiftUI
import CountryCodeKitSample

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            autoDemo: ProcessInfo.processInfo.environment["COUNTRY_CODE_DEMO"] == "1"
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
