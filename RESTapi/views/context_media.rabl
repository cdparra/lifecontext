collection @media, :root => "media", :object_root => false

attributes :caption => :title, :media_type => :type, :media_url => :url
attributes :source, :source_url

child :fuzzyDate => :time do |time|
  attributes :exact_date => :datetime unless time.exact_date.nil?
  attributes :season unless time.season.nil?
  attributes :year unless time.year.nil?
  attributes :day_name => :dayname unless time.day_name.nil?
  attributes :day_part => :daypart unless time.day_part.nil?
end

child :location do |loc|
  attributes :location_textual => :textual unless loc.location_textual.nil?
  attributes :name unless loc.name.nil?
  attributes :description unless loc.description.nil?
  attributes :city unless loc.city.nil?
  attributes :region unless loc.region.nil?
  attributes :country unless loc.country.nil?
  attributes :lat unless loc.lat.nil?
  attributes :lon unless loc.lon.nil?
end