class CreateChainentries < ActiveRecord::Migration
  def change
    create_table :chainentries do |t|
      t.date :day
      t.integer :chain_id

      t.timestamps
    end
  end
end
