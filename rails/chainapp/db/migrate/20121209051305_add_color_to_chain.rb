class AddColorToChain < ActiveRecord::Migration
  def change
      add_column :chains, :color, :string
  end
end
