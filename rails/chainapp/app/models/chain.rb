class Chain < ActiveRecord::Base
  attr_accessible :name
  has_many :chainentry, :dependent => :destroy
end
