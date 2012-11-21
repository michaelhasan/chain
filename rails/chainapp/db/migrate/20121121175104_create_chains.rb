class CreateChains < ActiveRecord::Migration
  def change
    create_table :chains do |t|
      t.string :name

      t.timestamps
    end
  end
end
