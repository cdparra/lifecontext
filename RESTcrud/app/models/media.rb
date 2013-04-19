class Media < ActiveRecord::Base
  self.table_name = "Media"
  belongs_to :location, :foreign_key => 'location_id', :primary_key => 'location_id'
  belongs_to :fuzzyDate, :foreign_key => 'fuzzy_startdate', :primary_key => 'fuzzy_date_id'
  
  accepts_nested_attributes_for :location, :fuzzyDate
  
end




