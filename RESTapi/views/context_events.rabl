collection @events, :root => "events", :object_root => false

attributes :headline
attributes :event_type => :type
attributes :text, :source, :source_url

child :fuzzyDate => :time do |time|
  attributes :exact_date => :datetime unless time.exact_date.nil?
  attributes :season unless time.season.nil?
  attributes :day_name => :dayname unless time.day_name.nil?
  attributes :day_part => :daypart unless time.day_part.nil?
  attributes :accuracy unless time.accuracy.nil?
end

child :location => :location do |loc|
  attributes :location_textual => :textual unless loc.location_textual.nil?
  attributes :name unless loc.name.nil?
  attributes :description unless loc.description.nil?
  attributes :city unless loc.city.nil?
  attributes :region unless loc.region.nil?
  attributes :country unless loc.country.nil?
  attributes :lat unless loc.lat.nil?
  attributes :lon unless loc.lon.nil?
  attributes :coordinates_trust => :trustness unless loc.coordinates_trust.nil?
end

attributes :locale