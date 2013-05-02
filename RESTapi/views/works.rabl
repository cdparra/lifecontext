collection @mediaMDs, :root => "works", :object_root => false

attributes :title, :type, :description, :source, :source_url
attributes :resource_url unless :resource_url.nil?

child :fuzzyDate => :time do |time|
  attributes :exact_date => :datetime unless time.exact_date.nil?
  attributes :accuracy unless time.accuracy.nil?
end

attributes :locale