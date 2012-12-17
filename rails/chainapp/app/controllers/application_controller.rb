class ApplicationController < ActionController::Base
  protect_from_forgery

  helper_method :current_user

  def current_user
    if session[:user_id]
      @current_user ||= User.find(session[:user_id])
    else
      @current_user = nil
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
      redirect_to "/auth/identity"
    end
  end

  def logged_in?
    if session[:user_id]
      return true
    else
      return false
    end
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
