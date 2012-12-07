class Chainentry < ActiveRecord::Base
  attr_accessible :chain_id, :day
  belongs_to :chain
  validates_existence_of :chain
  def to_param
     day.to_s
  end
end
