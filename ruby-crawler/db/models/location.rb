class Location < ActiveRecord::Base
  self.table_name = "Location"
  belongs_to :media, :foreign_key => "location_id", :primary_key => "location_id"
end
