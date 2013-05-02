class ContextIndex < ActiveRecord::Base
  self.table_name = "Context_Index"
  belongs_to :media, :foreign_key => 'media_id', :primary_key => 'media_id'
  belongs_to :mediaMetadata, :foreign_key => 'media_metadata_id', :primary_key => 'media_metadata_id'
  belongs_to :event, :foreign_key => 'event_id', :primary_key => 'event_id'
  belongs_to :city, :foreign_key => 'city_id', :primary_key => 'city_id'
end

class Participant < ActiveRecord::Base
  self.table_name = "Participant"
  belongs_to :person, :foreign_key => 'person_id', :primary_key => 'person_id'
  belongs_to :lifeEvent, :foreign_key => 'life_event_id', :primary_key => 'life_event_id'
end

class Person < ActiveRecord::Base
  self.table_name = "Person"
  has_many :participants
end

class LifeEvent < ActiveRecord::Base
  self.table_name = "Life_Event"
  self.inheritance_column = :ruby_type
  belongs_to :location, :foreign_key => 'location_id', :primary_key => 'location_id'
  belongs_to :fuzzyDate, :foreign_key => 'fuzzy_startdate', :primary_key => 'fuzzy_date_id'
  has_many :participant
end

class Media < ActiveRecord::Base
  self.table_name = "Media"
  belongs_to :location, :foreign_key => 'location_id', :primary_key => 'location_id'
  belongs_to :fuzzyDate, :foreign_key => 'fuzzy_startdate', :primary_key => 'fuzzy_date_id'
  has_one :contextIndex
end

class City < ActiveRecord::Base
  self.table_name = "City"
end

class Event < ActiveRecord::Base
  self.table_name = "Event"
  self.inheritance_column = :ruby_type
  belongs_to :location, :foreign_key => 'location_id', :primary_key => 'location_id'
  belongs_to :fuzzyDate, :foreign_key => 'fuzzy_startdate', :primary_key => 'fuzzy_date_id'
end

class MediaMetadata < ActiveRecord::Base
  self.table_name = "Media_Metadata"
  self.inheritance_column = :ruby_type
  belongs_to :fuzzyDate, :foreign_key => 'fuzzy_releasedate', :primary_key => 'fuzzy_date_id'
end

class FuzzyDate < ActiveRecord::Base
  self.table_name = "Fuzzy_Date"
  has_one :media
  has_one :event
  has_one :lifeEvent
end

class Location < ActiveRecord::Base
  self.table_name = "Location"
  has_one :media
  has_one :event
  has_one :lifeEvent

end

