class Chain < ActiveRecord::Base
  attr_accessible :name ,:color
  has_many :chainentry, :dependent => :destroy
end
