Pod::Spec.new do |s|
  s.name             = 'PPMagnesWrapper'
  s.version          = '0.1.0'
  s.summary          = 'PPMagnesWrapper'

  s.description      = <<-DESC
      PayPal Magnes Wrapper
                       DESC

  s.homepage         = 'https://github.com/mercadolibre/fury_pp-magnes-wrapper-ios'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'julio castillo' => 'julio.castillo@mercadolibre.com' }
  s.source           = { :git => 'git@github.com:mercadolibre/fury_pp-magnes-wrapper-ios.git', :tag => s.version.to_s }
  s.platform         = :ios, '10.0'
  s.static_framework = true
  s.swift_version = '5.0'

  s.source_files = 'LibraryComponents/Classes/**/*.{h,swift}'
  s.vendored_library = "LibraryComponents/Classes/Magnes/libPPRiskMagnesOC.a"
  s.frameworks = "Security", "CFNetwork", "MessageUI", "Foundation", "SystemConfiguration", "CoreLocation", "UIKit"
#  s.public_headers = 'LibraryComponents/Classes/Magnes/*.{h}'


end