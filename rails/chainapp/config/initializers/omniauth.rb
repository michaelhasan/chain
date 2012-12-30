
Rails.application.config.middleware.use OmniAuth::Builder do
   provider :identity
end

# omniauth is raising an exception in development mode otherwise
OmniAuth.config.on_failure = Proc.new { |env|
  OmniAuth::FailureEndpoint.new(env).redirect_to_failure
}
