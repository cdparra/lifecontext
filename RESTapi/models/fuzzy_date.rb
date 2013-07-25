class FuzzyDate < ActiveRecord::Base
  self.table_name = "Fuzzy_Date"
  belongs_to :media
  belongs_to :event
  belongs_to :creativeWork
  belongs_to :famousPeople
  belongs_to :lifeEvent
end
