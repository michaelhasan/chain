require 'test_helper'

class ChainentriesControllerTest < ActionController::TestCase
  setup do
    @chainentry = chainentries(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:chainentries)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create chainentry" do
    assert_difference('Chainentry.count') do
      post :create, :chainentry => { :chain_id => @chainentry.chain_id, :day => @chainentry.day }
    end

    assert_redirected_to chainentry_path(assigns(:chainentry))
  end

  test "should show chainentry" do
    get :show, :id => @chainentry
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @chainentry
    assert_response :success
  end

  test "should update chainentry" do
    put :update, :id => @chainentry, :chainentry => { :chain_id => @chainentry.chain_id, :day => @chainentry.day }
    assert_redirected_to chainentry_path(assigns(:chainentry))
  end

  test "should destroy chainentry" do
    assert_difference('Chainentry.count', -1) do
      delete :destroy, :id => @chainentry
    end

    assert_redirected_to chainentries_path
  end
end
