class SessionsController < ApplicationController
   
   def create
      auth = request.env["omniauth.auth"]
      if (User.find_by_provider_and_uid(auth["provider"], auth["uid"]))
         user = User.find_by_provider_and_uid(auth["provider"], auth["uid"])
      else
         user = User.create_with_omniauth(auth)
      end
      session[:user_id] = user.id
      respond_to do |format|
         format.html { redirect_to root_url, :notice => 'Signed in!'}
         format.json { render :json => user }
      end
   end 
 
   def destroy
      session[:user_id] = nil
      redirect_to root_url, :notice => "Goodbye!"
   end
  
#   def create
#      raise request.env["omniauth.auth"].to_yaml
#   end
end
