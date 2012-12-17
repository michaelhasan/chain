class AddUserIdToChain < ActiveRecord::Migration
   def change
      add_column :chains, :user_id, :int
   end
end
