require 'csv'

# Check if command line arguments are provided
if ARGV.length < 2
  puts "Usage: ruby script.rb <organiser>"
  exit
end

# Get event_id and organiser from command line arguments
organiser = ARGV[1]

# Read CSV file
csv_file = 'votes.csv'  # Replace with your actual CSV file path
csv_data = CSV.read(csv_file, headers: true)

# Create a new array to hold modified rows
modified_rows = []

# Add two additional columns at the beginning
csv_data.each do |row|
  row["organiser"] = organiser
  modified_rows << row
end

# Write modified data back to the CSV file
output_csv_file = 'processed_votes.csv'
CSV.open(output_csv_file, 'w', write_headers: true, headers: csv_data.headers) do |csv|
  modified_rows.each do |row|
    csv << row
  end
end


puts "Script executed successfully. Modified CSV saved at: #{output_csv_file}"
