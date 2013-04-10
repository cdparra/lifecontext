collection @works, :root => "works", :object_root => false

attributes :media_metadata_id
attributes :title, :type, :description, :source, :source_url
attributes :resource_url unless :resource_url.nil?

child :fuzzyDate => :time do |time|
  attributes :fuzzy_date_id
  attributes :exact_date => :datetime unless time.exact_date.nil?
  attributes :accuracy unless time.accuracy.nil?
end

attributes :locale