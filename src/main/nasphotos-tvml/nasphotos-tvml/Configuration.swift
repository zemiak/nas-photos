import Foundation

class Configuration {
    var config = [String: String]()

    init() {
        let configFileName = NSBundle.mainBundle().objectForInfoDictionaryKey("ConfigurationPlist") as! String
        let configFilePath = NSBundle.mainBundle().pathForResource(configFileName, ofType: "plist")!
        config = NSDictionary(contentsOfFile: configFilePath) as! [String: String]
    }

    func getBaseUrl() -> String {
        return config["BaseUrl"]!
    }

    func getMovieUrl() -> String {
        return config["MovieUrl"]!
    }

    func getEnvironment() -> String {
        return config["Environment"]!
    }

    func getVersion() -> String {
        return NSBundle.mainBundle().objectForInfoDictionaryKey("CFBundleShortVersionString") as! String
    }

    func isDevelopment() -> Bool {
        return "Development" == getEnvironment()
    }
}