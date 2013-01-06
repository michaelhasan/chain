class ApplicationController < ActionController::Base
  protect_from_forgery

  helper_method :current_user

  def current_user
    respond_to do |format|
       format.html {
          if session[:user_id]
            @current_user ||= User.find(session[:user_id])
          else
            @current_user = nil
          end
       }
       format.json {
          @current_user ||= User.find_by_uid(params["uid"])
       }
    end
  end

  def check_admin
    unless authorized?
      redirect_to "/auth/identity"
    logger.info 'check_admin redirecting'
    end
    logger.info 'check_admin successful'
  end

  def check_login
    unless logged_in?

      respond_to do |format|
         format.html { redirect_to "/auth/identity" }
         #format.json { render :json => user }
         format.json { render :json=> {:tag => "login", :error=>true, :success=>false, :error_message=>"Error with your login or password"}, :status=>401 }

      end
    end
  end

  def logged_in?
    respond_to do |format|
       format.html {
          if session[:user_id]
            return true
          else
            return false
          end
       }
       format.json {
          @current_user ||= User.find_by_uid(params["uid"])
          if @current_user == nil 
             return false   
          else
             return true
          end
       }
     end;
  end
  
  def check_current_user(user_id)
     unless is_current_user?(user_id)
        redirect_to "/auth/identity"
     end
  end
  
  protected
    def is_current_user?(user_id)
       user_id==current_user.id
    end

  protected
    def authorized?
      #logger.info "check_admin authorized: #{current_user.admin?}"
      logged_in? && current_user.admin?
    end

end
