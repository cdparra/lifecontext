class Point < Struct.new(:x, :y)
  # Generates a random point around (0,0) with radius = 1
  def self.random
    radius = rand
    theta = 360 * rand
    Point.new(radius*Math.cos(theta), radius*Math.sin(theta))
  end

  # Returns true if x is close to y as defined by eps
  def self.close_to(x, y, eps = 1e-9)
    return (x-y).abs < eps
  end

  def self.rad2deg(a)
    return (a * 180.0) / Math::PI
  end

  def normal_left
    Point.new(self.y, -self.x)
  end

  # Returns the point at the center of this point
  # and the other_point
  def center(other_point)
    Point.new((self.x + other_point.x)/2.0, (self.y + other_point.y)/2.0)
  end

  # Computes the distance between self and other_point
  def distance(other_point)
    ((self.x - other_point.x) ** 2 + (self.y - other_point.y) ** 2) ** 0.5
  end

  def self.inf_line_intersection(base1, v1, base2, v2)
    if (Point.close_to(v1.x, 0.0) && Point.close_to(v2.y, 0.0))
      return Point.new(base1.x, base2.y)
    end
    if (Point.close_to(v1.y, 0.0) && Point.close_to(v2.x, 0.0))
      return Point.new(base2.x, base1.y)
    end

    m1 = !Point.close_to(v1.x, 0.0) ? v1.y / v1.x : 0.0
    m2 = !Point.close_to(v2.x, 0.0) ? v2.y / v2.x : 0.0

    if Point.close_to(m1, m2)
      return nil
    end

    c1 = base1.y - m1 * base1.x
    c2 = base2.y - m2 * base2.x
    ix = (c2 - c1) / (m1 - m2)
    iy = m1 * ix + c1
    if Point.close_to(v1.x, 0.0)
      return Point.new(base1.x, base1.x * m2 + c2)
    end
    if Point.close_to(v2.x, 0.0)
      return Point.new(base2.x, base2.x * m1 + c1)
    end
    return Point.new(ix, iy)
  end

  # compute the polar angle of this point
  def polar
    theta = 0.0
    if Point.close_to(self.x, 0)
      if self.y > 0
        theta = 90.0
      elsif self.y < 0
        theta = 270.0
      end
    else
      theta = Point.rad2deg(Math.atan(self.y/self.x))
      if self.x < 0.0
        theta += 180.0
      end
      if theta < 0.0
        theta += 360.0
      end
    end
    theta
  end

  # Computes the angle formed by p1 - self - p2
  def angle_between(p1, p2)
    vect_p1 = p1 - self
    vect_p2 = p2 - self

    theta = vect_p1.polar - vect_p2.polar
    theta += 360.0 if theta < 0.0
    theta = 360.0 - theta if theta > 180.0
    return theta
  end

  def ==(other_point)
    return Point.close_to(self.x, other_point.x) && Point.close_to(self.y, other_point.y)
  end

  # We define sort order of Points based on x coordinate, then y coordinate
  def <=>(other_point)
    ret = self.x <=> other_point.x
    if ret == 0
      ret = self.y <=> other_point.y
    end
    ret
  end

  # Subtract other_point from self
  def -(other_point)
    Point.new(self.x - other_point.x, self.y - other_point.y)
  end

  # Add other_point to self
  def +(other_point)
    Point.new(self.x + other_point.x, self.y + other_point.y)
  end

  # Scale this point by the provided factor
  def scale(factor)
    Point.new(self.x * factor, self.y * factor)
  end

  # Pretty print a point
  def to_s
    "(%5.4f, %5.4f)" % [x, y]
  end
end 

class Circle < Struct.new(:center, :radius)
  # Returns true if this Circle contains the given Point
  def contains?(point)
    point.distance(self.center) < self.radius || (point.distance(self.center) - self.radius).abs < 1e-9
  end
  def to_s
    "{center:%s, radius:%5.4f}}" % [center.to_s, radius]
  end
end

require 'set'
class Solver < Struct.new(:mec)
  def initialize(debug)
    @points = SortedSet.new
    @debug = debug
  end

  # add a point to the solver and recalcluate the minimum enclosing
  # circle if the point lies outise the current MEC.
  def add_point(point)
    puts "adding p: #{point}" if @debug
    @points << point
    if self.mec && self.mec.center
      unless self.mec.contains?(point)
        iterate
      end
    else
      iterate
    end
  end

  # Tests if a point is left|on|right of a infinite line
  # return >0 for p2 left of line through p0 and p1
  # return = 0 for p2 on the line
  # return <0 for p2 right of the line
  def direction(p0, p1, p2)
    return (p0.x-p1.x)*(p2.y-p1.y) - (p2.x-p1.x)*(p0.y-p1.y)
  end

  # Computes the set of points that represents the
  # convex hull of the Solver's @points
  def convex_hull
    a = @points.to_a
    left = a.first
    a = a[1..-1]
    right = a.pop
    puts "left = #{left} right = #{right} a = #{a.map{|p| p.to_s}.join(", ")}" if @debug
    upper = []
    lower = []
    # Partition remaining points into upper and lower buckets
    a.each do |p|
      dir = direction(left, right, p)
      puts "p = #{p} dir = #{dir}" if @debug
      if dir < 0
        upper << p
      else
        lower << p
      end
    end
    puts "upper = #{upper.map{|p| p.to_s}.join(', ')}" if @debug
    puts "lower = #{lower.map{|p| p.to_s}.join(', ')}" if @debug
    upper_hull = half_hull(left, right, upper, -1)
    lower_hull = half_hull(left, right, lower, 1)
    puts "upper_hull = #{upper_hull.map{|p| p.to_s}.join(', ')}" if @debug
    puts "lower_hull = #{lower_hull.map{|p| p.to_s}.join(', ')}" if @debug
    (upper_hull + lower_hull).uniq
  end

  # Computs a half hull
  def half_hull(left, right, input, factor)
    input = input.dup
    input << right
    half = []
    half << left
    puts "input: #{input.map{|p| p.to_s}.join(', ')}" if @debug
    input.each do |p|
      half << p
      puts "half: #{half.map{|p| p.to_s}.join(', ')}" if @debug
      while half.size >= 3
        dir = factor * direction(half[-3], half[-1], half[-2])
        if dir <= 0
          half.delete_at(half.size-2)
        else
          break
        end
      end
    end
    half
  end

  # Recalculates the MEC after the addition of a new point
  def iterate
    self.mec ||= Circle.new
    # Handle degenerate cases first
    if @points.size == 1
      self.mec.center = @points.to_a[0]
      self.mec.radius = 0
    elsif @points.size == 2
      a = @points.to_a
      self.mec.center = a[0].center(a[1])
      self.mec.radius = a[0].distance(self.mec.center)
    else
      puts "points = #{@points.map{|p| p.to_s}.join(", ")}" if @debug
      a = convex_hull
      puts "convex hull = #{a.map{|p| p.to_s}.join(', ')}" if @debug
      point_a = a[0]
      point_c = a[1]
      while (true)
        point_b = nil
        best_theta = 180.0;
        for point in @points
          if point != point_a && point != point_c
            theta_abc = point.angle_between(point_a, point_c)
            puts "a: #{point_a} c: #{point_c} p: #{point} best_theta: #{best_theta} theta_abc: #{theta_abc}" if @debug
            if theta_abc < best_theta
              point_b = point
              best_theta = theta_abc
            end
          end
        end
        puts "Found best theta: #{best_theta}, a = #{point_a}, b = #{point_b}, c = #{point_c}" if @debug
        if best_theta >= 90.0 || point_b.nil?
          self.mec.center = point_a.center(point_c)
          self.mec.radius = point_a.distance(self.mec.center)
          return self.mec
        end
        ang_bca = point_c.angle_between(point_b, point_a)
        ang_cab = point_a.angle_between(point_c, point_b)
        puts "ang_bca = #{ang_bca}, ang_cab = #{ang_cab}" if @debug
        if ang_bca > 90.0
          point_c = point_b
        elsif ang_cab <= 90.0
          puts "breaking ..." if @debug
          break
        else
          point_a = point_b
        end
      end
      ch1 = (point_b - point_a).scale(0.5)
      ch2 = (point_c - point_a).scale(0.5)
      n1 = ch1.normal_left
      n2 = ch2.normal_left
      ch1 = point_a + ch1
      ch2 = point_a + ch2
      self.mec.center = Point.inf_line_intersection(ch1, n1, ch2, n2)
      self.mec.radius = self.mec.center.distance(point_a)
    end
    self.mec
  end
end

def distance(p1, p2)
  return p1.distance(p2)
end

# Use algorithm invented by Pr. Chrystal (1885)
# Port of code in a Java Applet found at
# http://www.personal.kent.edu/~rmuhamma/Compgeometry/MyCG/CG-Applets/Center/centercli.htm
# MEC Is determined by the Convex Hull of the set of points
# takes array of Point objects
# returns a Circle object
def encircle(points, debug = false)
  solver = Solver.new(debug)
  points.each { |p| solver.add_point(p) }
  puts "SOLVED: mec: #{solver.mec}" if debug
  return solver.mec
end

# Generate an array of random Point objects in the unit circle
# with center (0,0)
def generate_samples(n)
  (1..n).map { Point.random }
end

# For debugging, checks the solution and if it doesn't work, creates the image
# and reruns encircle in debug mode for the failed solution
def check_solution(points, circle)
  points.each do |point|
    if !circle.contains?(point)
      encircle(points, true)
      draw(points, circle, 'circle.png')
      puts "point #{point} (#{point.distance(circle.center)}) is outside of circle #{circle}"
      raise "BAD SOLUTION: point #{point} (#{point.distance(circle.center)}) is outside of circle #{circle}"
    end
  end
  return true
end

if __FILE__ == $0
  srand

  if ARGV[0] && ARGV[0] == 'benchmark'
    require 'benchmark'

    Benchmark.bm(15) do |x|
      5.times do |i|
        n = 10 ** i
        x.report("points: #{n}") do
          25.times do
            points = generate_samples(n)
            circle = encircle(points)
            check_solution(points, circle)
          end
        end
      end
    end
  elsif ARGV[0] && ARGV[0] == 'once'
    n = ARGV[1] ? ARGV[1].to_i : 15
    points = generate_samples(n)
    puts encircle(points)
  else
    puts "Usage: ruby mec.rb [draw [num_points]] | [benchmark] | [once [num_points]]"
    puts "     once: Runs the solution once and puts the circle"
    puts "        num_points defaults to 15 if not specified."
  end
end
